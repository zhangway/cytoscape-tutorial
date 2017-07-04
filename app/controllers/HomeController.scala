package controllers

import javax.inject._

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, Writes}
import play.api.mvc._

case class NData(id: String, label: String)
case class EData(id: String, source: String, target:String, width: Int)
case class NodeData(ndata: NData)
case class EdgeData(edata: EData)

case class CytoscapeData(nodes: Seq[NodeData], edges: Seq[EdgeData])

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  implicit val nDataWrites: Writes[NData] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "label").write[String]
  )(unlift(NData.unapply))

  implicit val eDataWrites: Writes[EData] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "source").write[String] and
    (JsPath \ "target").write[String] and
    (JsPath \ "width").write[Int]
  )(unlift(EData.unapply))

  implicit val nodeDataWrites: Writes[NodeData] =
    (JsPath \ "data").write[NData].contramap { (data: NodeData) => data.ndata}

  implicit val edgeDataWrites: Writes[EdgeData] =
    (JsPath \ "data").write[EData].contramap { (data: EdgeData) => data.edata}


  implicit val dataWrites: Writes[CytoscapeData] = (
    (JsPath \ "nodes").write[Seq[NodeData]] and
      (JsPath \ "edges").write[Seq[EdgeData]])(unlift(CytoscapeData.unapply))


  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def data() = Action { implicit request: Request[AnyContent] =>
    val nodes: Seq[NodeData] = Seq(
      NodeData(NData("a", "Gene1")),
      NodeData(NData("b", "Gene2")),
      NodeData(NData("c", "Gene3")),
      NodeData(NData("d", "Gene4")),
      NodeData(NData("e", "Gene5")),
      NodeData(NData("f", "Gene6"))
    )


    val edges: Seq[EdgeData] = Seq(
      EdgeData(EData("ab", "a", "b", 4)),
      EdgeData(EData("cd", "c", "d", 2)),
      EdgeData(EData("ef", "e", "f", 6)),
      EdgeData(EData("ac", "a", "c", 10)),
      EdgeData(EData("be", "b", "e", 20))
    )

    val cytoscapeData = CytoscapeData(nodes, edges)
    Ok(Json.toJson(cytoscapeData))
  }


}
