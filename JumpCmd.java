import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ActivitiEngineAgenda;

public class JumpCmd implements Command<Void> {

    private String executionId;   // 子执行流ID (Child Execution ID)
    private String targetNodeId;  // 目标节点ID (例如 "callReceived")

    public JumpCmd(String executionId, String targetNodeId) {
        this.executionId = executionId;
        this.targetNodeId = targetNodeId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        // 1. 获取执行流实体
        ExecutionEntity execution = commandContext.getExecutionEntityManager().findById(executionId);
        if (execution == null) {
            throw new RuntimeException("执行流不存在: " + executionId);
        }

        // 2. 获取流程定义模型 (BPMN Model)
        // Activiti 8 使用 ProcessDefinitionUtil 来获取缓存的流程定义
        Process process = ProcessDefinitionUtil.getProcess(execution.getProcessDefinitionId());
        FlowElement targetFlowElement = process.getFlowElement(targetNodeId);

        if (targetFlowElement == null) {
            throw new RuntimeException("目标节点不存在: " + targetNodeId);
        }

        // 3. 核心修复逻辑
        // A. 将当前执行流指向目标节点
        execution.setCurrentFlowElement(targetFlowElement);
        
        // B. 激活执行流 (防止它是挂起或未激活状态)
        execution.setActive(true);

        // C. 使用 Agenda 调度器触发执行
        // 这是 Activiti 7/8 与 旧版本最大的区别，必须通过 Agenda 计划下一步操作
        ActivitiEngineAgenda agenda = commandContext.getAgenda();
        agenda.planContinueProcessOperation(execution);

        return null;
    }
}
