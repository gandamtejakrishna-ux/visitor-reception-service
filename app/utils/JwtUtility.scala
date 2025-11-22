package utils

import com.typesafe.config.ConfigFactory
import pdi.jwt._
import play.api.libs.json._

import java.time.Instant
import scala.util.{Failure, Success}
/**
 * Utility responsible for creating and validating JWT tokens.
 *
 * Uses HS256 signing with a secret loaded from application.conf.
 * Stores username inside the JWT payload and supports token expiry.
 */
object JwtUtility {

  private val config = ConfigFactory.load()
  private val secret = config.getString("jwt.secret")
  private val expirySeconds = config.getInt("jwt.expiry")

  /**
   * Generates a signed JWT token for the given username.
   *
   * Payload contains:
   *  - "user": username
   *  - "iat": issued-at timestamp
   *  - "exp": expiration timestamp
   *
   * @param username the username to embed in the token
   * @return encoded JWT string signed with HS256
   */
  def createToken(username: String): String = {
    val now = Instant.now().getEpochSecond
    val exp = now + expirySeconds

    val claim = JwtClaim(
      content = Json.obj("user" -> username).toString,
      issuedAt = Some(now),
      expiration = Some(exp)
    )

    Jwt.encode(claim, secret, JwtAlgorithm.HS256)
  }

  /**
  * Validates the JWT token and extracts its JSON payload.
  *
  * @param token the JWT string from the Authorization header
    * @return Some(payloadJson) if valid, None if invalid or expired
  */
  def validateToken(token: String): Option[JsValue] = {
    Jwt.decode(token, secret, Seq(JwtAlgorithm.HS256)) match {
      case Success(claim) =>
        Some(Json.parse(claim.content))
      case Failure(_) =>
        None
    }
  }
}
