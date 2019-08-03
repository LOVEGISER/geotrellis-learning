package demo



package com.lightened.myproject


  import geotrellis.raster._
  import geotrellis.spark._
  import geotrellis.spark.io._
  import geotrellis.spark.io.file._
  import akka.actor._
  import akka.event.{Logging, LoggingAdapter}
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.model._
  import akka.http.scaladsl.server.Directives.{path, _}
  import akka.stream.{ActorMaterializer, Materializer}
  import demo.Main.dataBasePath
  import org.apache.hadoop.fs.Path

  import scala.concurrent._
  import geotrellis.raster.render.{ColorMap, ColorRamp, Png, RGB}
  import geotrellis.spark.io.hadoop.{HadoopAttributeStore, HadoopLayerReader}



  object Serve extends App with Service {
    //1：读取瓦片数据
    def dataBasePath = "/Users/wangleigis163.com/Documents/alex/dev/code/private/cloud-map/data-server/"
    var path=  dataBasePath+"/110000BJ_L5_TM_1990/title"
    val catalogPath = new java.io.File(path).getAbsolutePath
    val fileValueReader = FileValueReader(catalogPath)
    //2：基于瓦片数据定义数据渲染方法
    def reader(layerId: LayerId) = fileValueReader.reader[SpatialKey, BitArrayTile](layerId)
    //3:定义akka服务，并启动
    override implicit val system = ActorSystem("tutorial-system")
    override implicit val executor = system.dispatcher
    override implicit val materializer = ActorMaterializer()
    override val logger = Logging(system, getClass)
    Http().bindAndHandle(root,"localhost", 8080)
  }
   //类的伴生对象
  trait Service {
    implicit val system: ActorSystem
    implicit def executor: ExecutionContextExecutor
    implicit val materializer: Materializer
    val logger: LoggingAdapter
    //定义Colormap
    val colorRamp =
      ColorRamp(RGB(0,0,0), RGB(255,255,255))
        .stops(100)
        .setAlphaGradient(0xFF, 0xAA)

    val colorMap1 = ColorMap(Map(0 -> RGB(0,0,0), 1 -> RGB(255,255,255)))

    //定义http响应，这里想http响应内容修改为image/png，响应内存为图片的bytes形式
    def pngAsHttpResponse(png: Png): HttpResponse =
      HttpResponse(entity = HttpEntity(ContentType(MediaTypes.`image/png`), png.bytes))

    def root =
    // 根据zoom，x，y读取瓦片数据，具体个数为 http://localhost:8080/6/16/25
      pathPrefix(IntNumber / IntNumber / IntNumber) { (zoom, x, y) =>
        complete {
          Future {
            // Read in the tile at the given z/x/y coordinates.
            val tileOpt: Option[BitArrayTile] =
              try {
                Some(Serve.reader(LayerId("etlTest1", zoom)).read(x, y))
              } catch {
                case _: ValueNotFoundError =>
                  None
              }
            tileOpt.map { tile =>
              // Render as a PNG
              val png = tile.renderPng(colorMap1)
              pngAsHttpResponse(png)
            }
          }
        }
      } ~
        pathEndOrSingleSlash {
          getFromFile("static/index.html")
        } ~
        pathPrefix("") {
          getFromDirectory("static")
        }
  }
