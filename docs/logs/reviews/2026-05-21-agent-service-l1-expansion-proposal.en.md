---
level: L1
view: scenarios
module: agent-service
affects_level: L0, L1
affects_view: logical, process, development, scenarios
status: proposed
---

# Architecture Review Proposal: agent-service L1 Domain Expansion (Wave 1.2)

> **Date:** 2026-05-21
> **Author:** LucioIT (Core Architect) & 急急 (Agent)
> **Target Wave:** W0/W1 (Immediate Enforcement)
> **Related Rules:** Rule G-1 (Layered 4+1 Discipline), Rule R-G (Reactive I/O), Rule R-M (Engine Extraction)

## 1. 逻辑视图 (Logical View)
本模块从单体状态机向无状态、多态运行时演进，核心组件逻辑定义如下：

1. **多态派发器 (Polymorphic Dispatcher)**：支持本地函数调用与远程总线调用的统一入口。
2. **响应式协调器 (Reactive Orchestrator)**：负责任务的节拍控制、背压申请与 A2A 协议封装。
3. **任务中心 (Task Center)**：持久化管理 Task 控制状态。
4. **会话管理器 (Session Manager)**：管理中长程数据上下文，负责向计算节点进行“上下文投影”。
5. **执行引擎适配器 (Execution Engine Adapter)**：屏蔽 Workflow 与 ReAct 引擎差异，实现纯净计算注入。

## 2. 进程视图 (Process View)
聚焦于无状态计算与异步控制流：

1. **无状态执行闭环**：`Execute(TaskMetadata, InjectedContext) -> StateDelta`。引擎不持有状态，仅返回状态增量。
2. **响应式背压链路**：
   - **Local Push**：本地队列根据 `SkillCapacity` 申请 `request(N)`。
   - **Distributed Pull**：通过 `agent-bus` 转换为全局拉取信号。
3. **非阻塞挂起 (Yielding)**：弃用异常挂起，改为显式的 `Yield` 事件，由协调器释放线程并水合至持久化层。

## 3. 开发视图 (Development View)
遵循 Rule G-1.c 军规，代码目录与逻辑组件映射如下：

```text
agent-service/
├── src/main/java/com/huawei/ascend/agent/service/
│   ├── dispatcher/             # [逻辑组件] 多态派发器 (Dispatcher)
│   ├── orchestrator/           # [逻辑组件] 响应式协调器 (Orchestrator)
│   ├── task/                   # [逻辑组件] 任务中心 (Task Center)
│   ├── session/                # [逻辑组件] 会话管理器 (Session Manager)
│   └── engine/
│       ├── adapter/            # [逻辑组件] 执行引擎适配器 (Adapter)
│       └── spi/                # [军规] SPI 接口定义 (见附录)
```

## 4. 场景视图 (Scenarios View)
通过两个典型场景验证状态剥离逻辑：

1. **场景 A：Task 即 Session (单次任务)**
   - **流转**：Dispatcher 接收指令 -> SessionManager 返回空投影 -> Orchestrator 注入计算 -> 任务完成销毁上下文。
2. **场景 B：长程会话跨 Task 协作**
   - **流转**：新 Task 触发 -> SessionManager 根据历史记录进行“语义投影” -> Orchestrator 注入投影后的 Context -> 计算产生 Delta -> Delta 写回 Session。

## 5. 设计要点深度分解 (Deep-Dive Points)

### 5.1 A2A 协议深度集成
正式弃用 ADR-0016 缓期执行，全面嵌入 `a2a-java` SDK。Task 状态（Submitted/Working/Input-Required）与 A2A 标准强对齐，取代私有枚举。

### 5.2 状态生命周期定界 (Run ≤ Task ≤ Session ≤ Memory)
- **Run**：瞬时计算快照（计算指针、增量）。
- **Task**：控制状态（做没做完、为什么停）。
- **Session**：数据上下文（聊了什么、变量）。
- **Memory**：知识状态（我是谁、规则）。

### 5.3 无状态注入与上下文投影 (Context Injection)
执行引擎禁止主动 I/O。由 Service 层通过 `InjectedContext` 完成历史状态的“输液式”注入。长历史由 SessionManager 进行“投影（截断/摘要）”后注入。

### 5.4 响应式背压与多态部署
统一采用 Reactor `Sinks` 实现。边缘端表现为物理内存保护，云端表现为总线拉取保护，代码逻辑 1:1 复用。

### 5.5 任务控制状态与数据上下文剥离
TaskID 与 SessionID 逻辑解耦。支持一个 Session 内并发执行多个 Task，或者一个 Task 在多个 Session 间漂移（如群聊协作）。

### 5.6 接口协议 SPI (Engine ↔ Service)
定义核心计算协议：`AgentInvokeRequest(RunID, SessionContext, InjectedSkills)`。Service 负责 Read-Modify-Write 闭环，Engine 仅负责 `Pure-Function` 计算。

## 附录：SPI 接口清单 (Grounding Appendix)
1. `com.huawei.ascend.agent.service.engine.spi.StatelessEngine`
2. `com.huawei.ascend.agent.service.session.spi.ContextProjector`
3. `com.huawei.ascend.agent.service.task.spi.TaskStateStore`
