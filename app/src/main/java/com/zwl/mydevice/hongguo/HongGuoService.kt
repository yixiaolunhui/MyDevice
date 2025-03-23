package com.zwl.mydevice.hongguo

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * 红果 AccessibilityService 实现
 *
 * @author zwl
 * @describe 红果
 * @date 2025/3/2
 */
class HongGuoService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 获取当前窗口的根节点
        val rootNode = rootInActiveWindow ?: return
        // 查找 id 为 com.phoenix.read:id/ae4 的文本节点
        val textNodes = rootNode.findAccessibilityNodeInfosByViewId("com.phoenix.read:id/ae4")
        textNodes?.forEach { node ->
            val text = node.text
            // 判断文本是否包含“上滑继续观看短剧”或“上滑观看短剧”
            if (text != null && (text.contains("上滑继续观看短剧") || text.contains("上滑观看短剧"))) {
                // 当文本匹配时，查找 viewpager 节点（id：com.phoenix.read:id/l8）
                val viewPagerNodes = rootNode.findAccessibilityNodeInfosByViewId("com.phoenix.read:id/l8")
                if (!viewPagerNodes.isNullOrEmpty()) {
                    val viewPager = viewPagerNodes[0]
                    // 对 viewpager 执行向前滑动的动作，即切换到下一页
                    viewPager.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                }
            }
        }
    }

    override fun onInterrupt() {}
}