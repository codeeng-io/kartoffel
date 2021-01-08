# kartoffel

## Usage

Import desired async context:

```scala
import scala.concurrent.ExecutionContext.Implicits.global
import kartoffel.async.futureAsync
```

Available async contexts are:

```scala
import kartoffel.async.futureAsync
import kartoffel.async.cats.catsAsync
import kartoffel.async.zio.zioAsync
```

Create a serializer and a deserializer implicit values:

```scala
private implicit val serializer: CacheSerializer[String, String] = (value: String) => value
private implicit val deserializer: CacheDeserializer[String, String] = (serialized: String) => serialized
```

Create an instance of cache implementation:

```scala
val caffeineAsyncLoader: AsyncLoadingCache[String, DataEntry[String]] =
        Caffeine.newBuilder().buildAsync(_ => DataEntry.empty[String])
val cache = new CaffeineCache[String](caffeineAsyncLoader)
// Put value
val putResult: Future[String] = cache.put("test", "test")
val getResult: Future[String] = cache.get("test")
```
