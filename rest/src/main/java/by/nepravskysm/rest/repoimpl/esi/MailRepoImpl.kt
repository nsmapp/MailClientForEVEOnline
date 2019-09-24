package by.nepravskysm.rest.repoimpl.esi

import by.nepravskysm.domain.entity.InPutMail
import by.nepravskysm.domain.entity.OutPutMail
import by.nepravskysm.domain.repository.rest.mail.MailRepository
import by.nepravskysm.rest.api.EsiManager
import by.nepravskysm.rest.entity.request.MailRequest
import by.nepravskysm.rest.entity.subentity.Recipient

class MailRepoImpl(private val esiManager: EsiManager) : MailRepository{


    override suspend fun getMail(accessToken: String, characterId: Long, mailId: Long): InPutMail {
        val mail = esiManager
            .getMail(accessToken,
                characterId,
                mailId)
            .await()

        return InPutMail(mail.body,
            mail.from,
            mail.subject,
            mail.timestamp)
    }


    override suspend fun sendMail(
        accessToken: String,
        characterId: Long,
        outPutMail: OutPutMail
    ): Long {

        var recepientList = mutableListOf<Recipient>()
        for(recepient in outPutMail.recipients){
            recepientList.add(Recipient(recepient.recipientId,
                recepient.recipientType))
        }

        val mail = MailRequest(0,
            outPutMail.body,
            recepientList,
            outPutMail.subject)

        return esiManager.sendMail(accessToken,
            characterId,
            mail).await()
    }
}