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

#### 1.1.2 两种核心部署/集成模式

### 1.2 项目阶段背景与演进规划

### 1.3 设计原则与核心形态

#### 1.3.1 两种智能体形态的封装

#### 1.3.2 两种部署形态与集成调用方式（双模态）

#### 1.3.3 异构智能体兼容设计原则

#### 1.3.4 服务级背压与无状态原则（Reactive & Stateless）

#### 1.3.5 A2A 多智能体协同与双向对等网络（Agent-to-Agent Network）

#### 1.3.6 Task-Centric 状态控制与 A2A 中断信号体系

#### 1.3.7 双轨快慢路径调度原则 (Dual-Track Fast/Slow-Path Routing)

#### 1.3.8 异构引擎影子工具拦截与防腐原则 (Shadow-Plugin Anti-Corruption)

### 1.4 逻辑执行粒度与四层状态生命周期定界

### 1.5 Service 与 Engine 的核心分工界面原则

### 1.6 Message-Centric 数据域与 Task-Centric 控制域的分离原则

## 2. 场景视图 (Scenarios View)

### 2.1 高性能内聚运行场景 (共进程模式)

### 2.2 异构存量智能体兼容集成场景 (服务化模式)

### 2.3 跨节点多智能体 A2A 异步协同场景

### 2.4 高频轻量访问快路径场景 (Fast-Path Loop)

### 2.5 异构引擎影子工具挂起与异步执行场景 (Shadow-Plugin Intercept Loop)

## 3. 逻辑视图 (Logical View)

### 3.1 多态派发器 (Polymorphic Dispatcher)

### 3.2 引擎适配器 (Engine Adapter)

### 3.3 内部事件队列（Internal Event Queue）

### 3.4 A2A 协议收发引擎组件（A2A Connector）

### 3.5 Task-Centric 状态控制体系与信号派发组件

### 3.6 逻辑分工边界映射组件（Logical Boundary Mapping）

## 4. 进程视图 (Process View)

### 4.1 异步任务发布/消费环路 (Asynchronous Task Loop)

### 4.2 跨节点多智能体协作与中断唤醒链路 (A2A Collaboration Loop)

### 4.3 4级状态全生命周期流转环流（Four-Layer Life Cycle Flow）

### 4.4 双轨快慢路径调度时序流程 (Fast-Path & Slow-Path Dispatch Loop)

### 4.5 异构框架影子工具拦截与恢复流程 (Heterogeneous Framework Shadow Interceptor Flow)

## 5. 开发视图 (Development View)

### 5.1 依赖开源与自研边界定界

### 5.2 自研代码包目录映射与依赖集成

## 6. 物理视图 (Physical View)

### 6.1 共进程内聚部署拓扑 (Embedded Deployment)

### 6.2 存量解耦/异构微服务部署拓扑 (Decoupled Service Deployment)

### 6.3 双轨路径的物理存储与计算边界 (Dual-Track Physical & Compute Boundaries)

## 7. 附录：核心 SPI 接口 (Appendix: Core SPI Interfaces)

### 7.1 A2A 标准任务生命周期与中断类型定义

### 7.2 StatelessEngineExecutor 引擎核心契约接口定义

### 7.3 Dual-Track Router 与快慢路径处理器接口定义

### 7.4 ShadowToolInterceptor 与异构适配器接口定义
