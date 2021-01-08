/*
 * Copyright (c) 2021 codeeng
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package kartoffel.caffeine

import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import kartoffel.CacheAlgebra
import kartoffel.async.Async
import kartoffel.formats.{ CacheDeserializer, CacheSerilizer }

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

case class DataEntry[S](data: Option[S] = None)

object DataEntry {
  def empty[S]: DataEntry[S] = DataEntry(Option.empty[S])
}

class CaffeineCache[S](cache: AsyncLoadingCache[String, DataEntry[S]]) extends CacheAlgebra[S] {

  override def put[F[_], V](key: String, value: V)(implicit
      cacheSerializer: CacheSerilizer[V, S],
      async: Async[F]
  ): F[V] = {
    val completionStage = CompletableFuture.supplyAsync(() => DataEntry(Option(cacheSerializer.serialize(value))))
    cache.put(key, completionStage)
    completionStageToAsync(completionStage, async)(_ => value)
  }

  override def get[F[_], V](
      key: String
  )(implicit cacheDeserializer: CacheDeserializer[V, S], async: Async[F]): F[Option[V]] =
    completionStageToAsync(cache.get(key), async) { dataEntry =>
      dataEntry.data.map(v => cacheDeserializer.deserialize(v))
    }

  private def completionStageToAsync[F[_], T, V](completionStage: CompletionStage[DataEntry[T]], async: Async[F])(
      handleDataEntry: (DataEntry[T]) => V
  ): F[V] =
    async.handle[V] { callback =>
      completionStage.handle[Unit] { (dataEntry, t) =>
        t match {
          case null => callback(Right(handleDataEntry(dataEntry)))
          case ex   => callback(Left(ex))
        }
      }
    }
}
