import com.huawei.hms.advancedlocationlibrary.data.model.holders.Result

fun interface TaskListener<T> {
    fun onCompleted(result: Result<T>)
}