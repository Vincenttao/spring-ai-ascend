---
level: L1
view: [scenarios, logical, process, development, physical]
module: agent-service
affects_level: L0, L1
affects_view: [scenarios, logical, process, development, physical]
status: proposed
---

# 架构评审提案：agent-service L1 领域扩展 (Wave 1.2)

> **日期:** 2026-05-21
> **作者:** LucioIT (核心架构师) & 急急 (智能体)
> **目标 Wave:** W0/W1 (立即执行)
> **关联军规:** Rule G-1.c (L1 深度与落地), Rule R-G (响应式 I/O), Rule R-M (引擎剥离)

## 1. 背景与原则 (Background & Principles)

### 1.1 顶层设计背景 (L0 架构)
本模块（agent-service）作为整体智能体生态中的核心一环，深度嵌入在 **L0 顶层设计架构**之中。L0 架构整体由 **6 大核心模块** 与 **2 种执行模式** 构成：

#### 1.1.1 六大核心模块
1. **智能体客户端 (agent-client)**：在 SaaS 应用与桌面应用中被集成，负责感知业务知识与状态，操作业务环境与工具，下发管理智能体配置，调用执行智能体服务。
2. **智能体服务端 (agent-service)**：**（本模块核心定界）** 负责把图模式执行的 workflow 智能体与循环模式执行的 ReAct 智能体封装成微服务。
3. **智能体执行引擎 (agent-execution-engine)**：负责提供两大类智能体的执行器，提供可供开发者使用的各种组件，如 workflow 会用到的 node、ReAct 会用到的 tool 和 hook。
4. **智能体总线 (agent-bus)**：负责连接南北向的 C/S 通信流量，连接东西向的 A2A 通信流量。
5. **智能体中间件 (agent-middleware)**：负责提供智能体需要的基础服务，如记忆服务、技能服务、知识服务、沙箱服务等。
6. **智能体演进平台 (agent-evolve)**：负责在线与离线的智能体自主演进。

#### 1.1.2 两种核心执行模式
- **图模式 (Workflow/Graph Mode)**：通过工作流或有向图，将多步骤、多节点（Node）的结构化计算拓扑化，适合确定性强的复杂业务。
- **循环模式 (ReAct/Loop Mode)**：通过 Agent 循环调用工具（Tool）和钩子（Hook），基于推理与动作的自主闭环，适合非确定性的动态决策任务。

## 2. 场景视图 (Scenarios View)

## 3. 逻辑视图 (Logical View)

## 4. 进程视图 (Process View)

## 5. 开发视图 (Development View)

## 6. 物理视图 (Physical View)

## 7. 附录：核心 SPI 接口 (Appendix: Core SPI Interfaces)
