import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProcessFixService {

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 修复僵尸节点：强制重启指定节点的执行，重新生成定时器
     * @param businessKeys 出现问题的业务Key列表
     * @param activityId   当前卡住的节点ID (即你的 callReceived)
     */
    @Transactional(rollbackFor = Exception.class)
    public void fixZombieProcess(List<String> businessKeys, String activityId) {
        for (String busKey : businessKeys) {
            try {
                // 1. 根据 BusinessKey 查找流程实例
                ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                        .processInstanceBusinessKey(busKey)
                        .singleResult();

                if (instance == null) {
                    System.out.println("Key [" + busKey + "] 未找到活跃流程，跳过。");
                    continue;
                }

                String procInstId = instance.getId();
                System.out.println("正在修复流程: " + procInstId + " (Key: " + busKey + ")");

                // 2. 核心魔法：状态迁移 (Move to Self)
                // 这会将当前停留在 activityId 的 Token 杀掉，并在同位置重新生成一个
                // 副作用：会重新触发该节点上定义的 boundary timer event
                runtimeService.createChangeActivityStateBuilder()
                        .processInstanceId(procInstId)
                        .moveActivityIdTo(activityId, activityId) 
                        .changeState();

                System.out.println("✅ 修复成功: 定时器已重新生成");

            } catch (Exception e) {
                System.err.println("❌ 修复失败 [" + busKey + "]: " + e.getMessage());
                // e.printStackTrace();
            }
        }
    }
}
