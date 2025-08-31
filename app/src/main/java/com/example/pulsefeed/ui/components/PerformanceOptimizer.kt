package com.example.pulsefeed.ui.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

object PerformanceOptimizer {
    @Composable
    fun rememberLazyListState(
        initialFirstVisibleItemIndex: Int = 0,
        initialFirstVisibleItemScrollOffset: Int = 0
    ): LazyListState {
        return androidx.compose.foundation.lazy.rememberLazyListState(
            initialFirstVisibleItemIndex,
            initialFirstVisibleItemScrollOffset
        )
    }
}

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@Composable
fun LazyListState.isScrollingDown(): Boolean = !isScrollingUp()

@Composable
fun LazyListState.reachedBottom(buffer: Int = 1): Boolean {
    val layoutInfo = layoutInfo
    return layoutInfo.visibleItemsInfo.lastOrNull()?.index != -1 &&
            layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - buffer
}

// Optimized image loading with memory management
object ImageCache {
    private val cache = mutableMapOf<String, Any>()
    private const val MAX_CACHE_SIZE = 50
    
    fun put(key: String, value: Any) {
        if (cache.size >= MAX_CACHE_SIZE) {
            cache.remove(cache.keys.first())
        }
        cache[key] = value
    }
    
    fun get(key: String): Any? = cache[key]
    
    fun clear() = cache.clear()
}

// Performance monitoring
class PerformanceMonitor {
    private var frameStartTime = 0L
    private var frameCount = 0
    private var totalFrameTime = 0L
    
    fun startFrame() {
        frameStartTime = System.nanoTime()
    }
    
    fun endFrame() {
        val frameTime = System.nanoTime() - frameStartTime
        totalFrameTime += frameTime
        frameCount++
        
        // Log if frame took too long (>16ms for 60fps)
        if (frameTime > 16_000_000) {
            println("Slow frame detected: ${frameTime / 1_000_000}ms")
        }
    }
    
    fun getAverageFrameTime(): Long {
        return if (frameCount > 0) totalFrameTime / frameCount else 0
    }
    
    fun reset() {
        frameCount = 0
        totalFrameTime = 0
    }
}

// Memory-efficient list item visibility tracking
@Composable
fun LazyListState.visibleItemsInfo(): List<androidx.compose.foundation.lazy.LazyListItemInfo> {
    return remember(this) {
        derivedStateOf {
            layoutInfo.visibleItemsInfo
        }
    }.value
}

// Optimized scroll to item with animation
suspend fun LazyListState.animateScrollToItemOptimized(
    index: Int,
    scrollOffset: Int = 0
) {
    if (index < layoutInfo.totalItemsCount) {
        animateScrollToItem(index, scrollOffset)
    }
}

// Debounced state for search and input fields
@Composable
fun <T> rememberDebouncedState(
    value: T,
    delayMillis: Long = 300L
): State<T> {
    val debouncedValue = remember { mutableStateOf(value) }
    
    LaunchedEffect(value) {
        kotlinx.coroutines.delay(delayMillis)
        debouncedValue.value = value
    }
    
    return debouncedValue
}

// Viewport-based lazy loading
@Composable
fun LazyListState.isItemVisible(index: Int): Boolean {
    return layoutInfo.visibleItemsInfo.any { it.index == index }
}

// Memory pressure detection
object MemoryManager {
    fun isLowMemory(): Boolean {
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory
        
        return usedMemory > maxMemory * 0.8 // 80% threshold
    }
    
    fun requestGarbageCollection() {
        if (isLowMemory()) {
            System.gc()
        }
    }
}
