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
    def dataBasePath = "/Users/wangleigis163.com/Documents/alex/dev/code/private/cloud-map/data-server/"
   var path=  dataBasePath+"/110000BJ_L5_TM_1990/title"
    val catalogPath = new java.io.File(path).getAbsolutePath
    val fileValueReader = FileValueReader(catalogPath)
    def reader(layerId: LayerId) = fileValueReader.reader[SpatialKey, BitArrayTile](layerId)

    override implicit val system = ActorSystem("tutorial-system")
    override implicit val executor = system.dispatcher
    override implicit val materializer = ActorMaterializer()
    override val logger = Logging(system, getClass)

    Http().bindAndHandle(root,"localhost", 8080)
  }

  trait Service {
    implicit val system: ActorSystem
    implicit def executor: ExecutionContextExecutor
    implicit val materializer: Materializer
    val logger: LoggingAdapter

    val colorRamp =
      ColorRamp(RGB(0,0,0), RGB(255,255,255))
        .stops(100)
        .setAlphaGradient(0xFF, 0xAA)

    val colorMap1 =
      ColorMap(
        Map(
          0 -> RGB(0,0,0),
          1 -> RGB(255,255,255)
        )
      )


    def pngAsHttpResponse(png: Png): HttpResponse =
      HttpResponse(entity = HttpEntity(ContentType(MediaTypes.`image/png`), png.bytes))

    def root =
    //    http://localhost:8080/6/16/25
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
