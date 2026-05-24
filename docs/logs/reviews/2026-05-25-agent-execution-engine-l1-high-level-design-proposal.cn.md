---
level: L1
view: [scenarios, logical, process, development, physical]
module: agent-execution-engine
affects_level: L0, L1
affects_view: [scenarios, logical, process, development, physical]
status: proposed
---

# 架构评审提案：agent-execution-engine L1 高级设计提案 (Wave 1.2)

> **日期:** 2026-05-25
> **作者:** LucioIT (核心架构师) & 急急 (智能体)
> **目标 Wave:** W0/W1 (立即执行)
> **关联军规:** Rule G-1.c (L1 深度与落地)

## 1. 背景与原则 (Background & Principles)

### 1.1 顶层设计背景 (L0 架构)

#### 1.1.1 六大核心模块
1. **智能体客户端 (agent-client)**：在 SaaS 应用与桌面应用中被集成，负责感知业务知识与状态，操作业务环境与工具，下发管理智能体配置，调用执行智能体服务。
2. **智能体服务端 (agent-service)**：负责把图模式执行 of workflow 智能体与循环模式执行 of ReAct 智能体封装成微服务。
3. **智能体执行引擎 (agent-execution-engine)**：**（本模块核心定界）** 负责提供两大类智能体的执行器，提供可供开发者使用的各种组件，如 workflow 会用到的 node、ReAct 会用到的 tool 和 hook。
4. **智能体总线 (agent-bus)**：负责连接南北向 of C/S 通信流量，连接东西向 of A2A 通信流量。
5. **智能体中间件 (agent-middleware)**：负责提供智能体需要的基础服务，如记忆服务、技能服务、知识服务、沙箱服务等。
6. **智能体演进平台 (agent-evolve)**：负责在线与离线的智能体自主演进。

#### 1.1.2 两种核心部署/集成模式
- **平台中心模式 (Platform-Centric Mode)**：业务侧仅集成 `agent-client`，其他所有模块均部署在平台端（集中托管与运行，降低业务集成心智负担）。
- **业务中心模式 (Business-Centric Mode)**：业务侧不仅集成 `agent-client`，还会在本地化（业务物理边界内）部署 `agent-service` 和 `agent-execution-engine`，实现就近计算；平台侧仅提供统一治理、互联互通及基础公共服务。

### 1.2 项目阶段背景与演进规划

#### 1.2.1 定位：基于开源基座增补的工具集引擎 (Open-Source Foundation & Core Engine)
根据 **L0 顶层架构定位**，`agent-execution-engine` 本身不参与具体垂直业务智能体的开发，而是作为智能体执行的“通用物理芯片”。
- **基于开源底座快速迭代**：本模块的发展并非闭门造车，而是**基于成熟开源项目 `openJiuwen/agent-core-java` 仓库作为起点和物理底座进行深度改造、增补与增量迭代**。我们将复用其已具备的底层执行器核心功能，并针对平台化需求进行分布式、无状态、响应式及 A2A 协议层面的改造。
- **工具非智能体**：本引擎仅提供基础执行器、通用组件及开发工具，具体的业务逻辑由使用工具的开发者（平台用户）自行构建。

#### 1.2.2 开发者体验 (DX) 演进路线（三阶段跃迁）
为了降低开发门槛、释放生产力，我们为引擎规划了清晰的开发者体验演进 roadmap：
1. **阶段一（当前 Wave 1 聚焦）：配置化开发 (Configuration-Driven Development)**
   - 彻底将智能体逻辑与代码解耦。无论是 Workflow 还是 ReAct 智能体，其核心运行拓扑、工具绑定关系、以及 Hook 配置全部支持**声明式标准 schema（JSON/YAML）定义**。支持配置一键解析、动态加载并在生产环境直接部署。
2. **阶段二：图形化开发 (Graphical Flow Canvas)**
   - 抽象并提供可视化的低代码拖拽画布（Flow Chart），支持开发者直观地对 Node、Tool、Hook 进行拓扑连接，并一键导出标准配置文件。
3. **阶段三（终极杀手锏）：自然语言式开发 (Natural Language-Driven Development)**
   - **对话式构建**：开发者用户在跟“元智能体（Meta Agent）”聊天的过程中，直接讨论和细化业务 SOP。
   - **交互式装配**：类似 openClaw 的交互体验，用户可通过自然语言指令“安装这个工具/注入那个钩子”，元智能体实时感知并翻译为引擎底层配置指令。
   - **自动生成与调试**：系统自动在后台合成、编排并进行运行时调试，最终一键导出符合生产部署要求的标准配置包。

### 1.3 设计原则与核心形态

#### 1.3.1 核心形态：双驱运行内核 (Dual-Engine Architecture)
1. **工作流引擎（Workflow / Graph-Mode Executor）**：
   - **拓扑执行**：支持有向无环（DAG）以及带有复杂环形/条件跳转的复杂图（Graph）拓扑调度。
   - **积木化节点（Standard Nodes）**：将常见计算步骤固化为原子级 Node 组件（如 LLM 推理节点、Prompt 渲染节点、条件决策路由节点、数据收集与映射节点等），开发者只需通过 DSL 或 Java API 编排节点关系。
2. **ReAct 引擎（ReAct / Loop-Mode Executor）**：
   - **循环推进（Reasoning-Action Loop）**：负责维护“思考(Thought) -> 动作(Action) -> 观察(Observation)”的自主推理闭环。
   - **组件级支撑（Tools & Hooks）**：提供标准的 **Tool（物理工具适配）** 与 **Hook（生命周期钩子）**。通过 Tool 屏蔽外部系统的物理调用；通过 Hook 支持在 Loop 运行的前置、中置、后置注入统一的安全审计、风控拦截和日志追踪。

#### 1.3.2 设计原则：无状态芯片与声明式/对话式生成 (Stateless & Generative Tenets)
1. **完全无状态（Stateless Compute Kernel）**：
   - 引擎内核坚守“纯计算芯片”原则，不直连数据库、不发起直接 A2A 网络寻址。
   - 每次执行均为 pure 状态映射过程。一切阻断或外部依赖通过向 Service 抛出强类型 **`InterruptSignal`** 解决，实现计算线程的零阻塞与极致并发。
2. **配置与代码彻底分离（Configuration & Code Decoupling）**：
   - 智能体逻辑的承载是“配置”而非“编译后代码”。任何 Workflow 的图节点定义和 ReAct 的决策树均可声明化，使得运行拓扑可在运行时被修改、翻译和序列化。
3. **自然语言式开发友好（NL-Dev Friendly Design）**：
   - 引擎设计支持高度的**动态化反射与热插拔**（如动态热插拔 Tool、动态替换节点），底层逻辑必须提供自省（Introspection）接口。这为阶段三的“元智能体”在对话过程中动态解析 SOP、合成配置、并在运行时环境就地动态热加载进行调试提供了坚实的底层技术支撑。
4. **防腐与异构引擎兼容（Heterogeneous Engine Friendly）**：
   - 提供标准化的抽象层，能够以统一的 SPI 屏蔽不同大模型（LLMs）以及底层物理框架的异构差异。

## 2. 场景视图 (Scenarios View)

## 3. 逻辑视图 (Logical View)

## 4. 进程视图 (Process View)

## 5. 开发视图 (Development View)

## 6. 物理视图 (Physical View)

## 7. 附录：核心 SPI 接口 (Appendix: Core SPI Interfaces)
