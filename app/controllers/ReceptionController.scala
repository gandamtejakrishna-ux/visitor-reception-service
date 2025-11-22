package controllers

import play.api.Configuration
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import javax.inject._

/**
 * Handles visitor check-in and check-out requests at the reception service.
 *
 * This controller validates incoming JSON, forwards requests
 * to the Visitor-Processing service, and returns the same status back to the client.
 */
@Singleton
class ReceptionController @Inject()(
                                     cc: ControllerComponents,
                                     ws: WSClient,
                                     config: Configuration,
                                   secured: actions.SecuredAction
                                   )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  private val baseUrl: String =
    config.getOptional[String]("downstream.visitor-processing.url")
      .getOrElse(throw new Exception("VISITOR_PROCESSING_URL not configured"))

  private def checkinUrl  = s"$baseUrl/api/visitors/checkin"
  private def checkoutUrl(id: Long) = s"$baseUrl/api/visitors/checkout/$id"


  /**
   * Receives check-in request from receptionist UI, validates fields,
   * then forwards the JSON to the visitor-processing service.
   *
   * @return Created/BadRequest based on downstream response.
   */
  def checkin: Action[JsValue] = secured.async(parse.json) { request =>
    val json = request.body

    val visitor      = (json \ "visitor").asOpt[JsObject]
    val hostId       = (json \ "hostEmployeeId").asOpt[Long]
    val purpose      = (json \ "purpose").asOpt[String]
    val aadhaarOpt   = (json \ "idProof" \ "aadhaarNumber").asOpt[String]


    // Validate required fields
    if (visitor.isEmpty || hostId.isEmpty || purpose.isEmpty || aadhaarOpt.isEmpty) {
      Future.successful(BadRequest(Json.obj("error" -> "Missing required fields")))
    }
    else if (!aadhaarOpt.get.matches("^[0-9]{12}$")) {
      Future.successful(BadRequest(Json.obj("error" -> "Invalid Aadhaar (must be 12 digits)")))
    }
    else {
      ws.url(checkinUrl)
        .post(json)
        .map(res => Status(res.status)(res.body))
    }
  }


  /**
   * Forwards a check-out request to the visitor-processing service.
   *
   * @param id Visit ID to check out.
   * @return Ok/NotFound based on downstream response.
   */
  def checkout(id: Long): Action[AnyContent] = secured.async {
    ws.url(checkoutUrl(id))
      .post(Json.obj())
      .map(res => Status(res.status)(res.body))
  }

  // GET /api/visitors/:id
  def getVisitor(id: Long): Action[AnyContent] = secured.async {
    ws.url(s"$baseUrl/api/visitors/$id")
      .get()
      .map(res => Status(res.status)(res.body))
  }

  // GET /api/visits/active
  def activeVisits: Action[AnyContent] = secured.async {
    ws.url(s"$baseUrl/api/visits/active")
      .get()
      .map(res => Status(res.status)(res.body))
  }

}
