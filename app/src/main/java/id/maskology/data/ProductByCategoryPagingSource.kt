package id.maskology.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.maskology.data.model.Category
import id.maskology.data.model.Product
import id.maskology.data.remote.api.ApiService

class ProductByCategoryPagingSource (private val apiService: ApiService, private val category: String ) : PagingSource<Int, Product>() {
    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getAllProductByCategory(position, params.loadSize, category)
            LoadResult.Page(
                data = responseData.listProduct,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listProduct.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

}