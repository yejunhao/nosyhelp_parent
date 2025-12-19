import org.activiti.engine.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZombieFixService {

    @Autowired
    private ManagementService managementService;

    /**
     * 修复 Activiti 8 僵尸节点
     * @param childExecutionIds 子执行流ID列表 (通过之前的 SQL 查出来的 ID_)
     * @param targetNodeId      目标节点ID (例如 "callReceived")
     */
    public void fixActiviti8Zombies(List<String> childExecutionIds, String targetNodeId) {
        for (String execId : childExecutionIds) {
            try {
                System.out.println("正在修复子执行流: " + execId);
                // 执行自定义命令
                managementService.executeCommand(new JumpCmd(execId, targetNodeId));
                System.out.println("✅ 修复成功，定时器应已重建");
            } catch (Exception e) {
                System.err.println("❌ 修复失败 [" + execId + "]: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
