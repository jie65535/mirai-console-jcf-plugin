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
    private var hasNext = false
    fun getHasNext() = hasNext

    suspend fun prev(): Array<T> {
        if (pageIndex > 0) {
            pageIndex--
        }
        return current()
    }

    suspend fun next(): Array<T> {
        if (hasNext) {
            pageIndex++
        }
        return current()
    }

    suspend fun current(): Array<T> {
        return if (pageIndex < pages.size) {
            pages[pageIndex]
        } else {
            val data = getPageData(pageIndex)
            hasNext = data.size == pageSize
            pages.add(data)
            data
        }
    }
}