package top.jie65535.jcf.util

/**
 * 分页的列表
 * @param getPageData 搜索新数据方法
 */
class PagedList<T>(
    private val pageSize: Int,
    private val getPageData: suspend (pageIndex: Int) -> Array<T>
) {
    private val pages = mutableListOf<Array<T>>()
    private var pageIndex = 0
    private var _hasNext = false
    val hasNext get() = _hasNext
    val hasPrev get() = pageIndex > 0

    suspend fun prev(): Array<T> {
        if (pageIndex > 0) {
            _hasNext = true
            pageIndex--
        }
        return current()
    }

    suspend fun next(): Array<T> {
        if (_hasNext) {
            pageIndex++
        }
        return current()
    }

    suspend fun current(): Array<T> {
        return if (pageIndex < pages.size) {
            pages[pageIndex]
        } else {
            val data = getPageData(pageIndex * pageSize)
            _hasNext = data.size == pageSize
            pages.add(data)
            data
        }
    }
}
