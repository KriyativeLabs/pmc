package helpers

import java.io.File

import play.api.Logger
import play.api.libs.mailer.{AttachmentFile, Email, MailerClient}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class EmailService(implicit val mailerClient: MailerClient) {
  val logger: Logger = Logger(this.getClass)
  val FROM = "PayMyCable<paymycable@gmail.com>"

  def sendSimpleMail(subject: String, to: List[String], message: String) = Future {
    try {
      mailerClient.send(Email(subject, FROM, to, bodyText = Some(message)))
    } catch {
      case e: Throwable => logger.error("Error Sending Message:", e)
    }
  }

  def sendHtmlMail(subject: String, to: List[String], htmlMessage: String) = Future {
    try {
      mailerClient.send(Email(subject, FROM, to, bodyHtml = Some(htmlMessage)))
    } catch {
      case e: Throwable => logger.error("Error Sending Message:", e)
    }
  }

  def sendMailWithAttachments(subject: String, to: List[String], htmlMessage: String, attachments:List[String]) = Future{
    val mailAttachments = attachments.map({x =>
      val fileSplit = x.split(File.separator)
      AttachmentFile(fileSplit(fileSplit.length - 1), new File(x))
    })
    mailerClient.send(Email(subject, FROM, to,attachments = mailAttachments,  bodyHtml = Some(htmlMessage)))
  }
}