package by.nepravskysm.mailclientforeveonline.di

import by.nepravskysm.database.AppDatabase
import by.nepravskysm.database.repoimpl.AuthInfoRepoImpl
import by.nepravskysm.domain.repository.database.AuthInfoRepository
import by.nepravskysm.domain.repository.rest.auth.AuthRepository
import by.nepravskysm.domain.repository.rest.auth.CharacterInfoRepository
import by.nepravskysm.domain.repository.rest.mail.MailRepository
import by.nepravskysm.domain.repository.rest.mail.MailsHeadersRepository
import by.nepravskysm.domain.repository.utils.IdsRepository
import by.nepravskysm.domain.repository.utils.NamesRepository
import by.nepravskysm.domain.usecase.auth.AuthUseCase
import by.nepravskysm.domain.usecase.character.GetActivCharInfoUseCase
import by.nepravskysm.domain.usecase.mails.GetMailUseCase
import by.nepravskysm.domain.usecase.mails.GetMailsHeaderUseCase
import by.nepravskysm.domain.usecase.mails.SendMailUseCase
import by.nepravskysm.mailclientforeveonline.presentation.main.MainViewModel
import by.nepravskysm.mailclientforeveonline.presentation.main.fragments.maillists.base.MailListViewModel
import by.nepravskysm.mailclientforeveonline.presentation.main.fragments.newmail.NewMailViewModel
import by.nepravskysm.mailclientforeveonline.presentation.main.fragments.readmail.ReadMailViewModel
import by.nepravskysm.rest.api.AuthManager
import by.nepravskysm.rest.api.EsiManager
import by.nepravskysm.rest.repoimpl.auth.AuthRepoImpl
import by.nepravskysm.rest.repoimpl.esi.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module


val restModule: Module = module {

    single { AuthManager() }
    single { EsiManager() }

    factory<AuthRepository> { AuthRepoImpl(authManager = get()) }
    factory<CharacterInfoRepository>{
        CharacterInfoRepoImpl(
            esiManager = get()
        )
    }

    factory<MailsHeadersRepository> { MailsHeadersRepoImpl(esiManager = get())  }
    factory<MailRepository> { MailRepoImpl(esiManager = get())  }
    factory<NamesRepository> { NamesRepoImpl(esiManager = get()) }
    factory<IdsRepository> {IdsRepoImpl(esiManager = get())}

}

val databaseModule: Module = module {

    single<AppDatabase> { AppDatabase.getInstance(androidContext())}

    factory<AuthInfoRepository> {AuthInfoRepoImpl(appDatabase = get())}
}

val useCaseModule: Module = module {

    factory{ AuthUseCase(authRepository = get(),
        authInfoRepository = get(),
        characterInfoRepository = get())  }

    factory { GetMailsHeaderUseCase(authRepository = get(),
        authInfoRepository = get(),
        mailsHeadersRepository = get(),
        namesRepository = get()) }

    factory { GetActivCharInfoUseCase(authInfoRepository = get()) }
    factory { GetMailUseCase(authRepository = get(),
        authInfoRepository = get(),
        mailRepository = get()) }

    factory { SendMailUseCase(authRepository = get(),
        authInfoRepository = get(),
        mailRepository = get(),
        idsRepository = get()) }
}

val viewModelModule: Module = module {
    viewModel { MainViewModel(authUseCase = get(), getActivCharInfoUseCase = get()) }
    viewModel {
        MailListViewModel(
            getMailsHeaderUseCase = get()
        )
    }
    viewModel { ReadMailViewModel(getMailUseCase = get()) }
    viewModel { NewMailViewModel(sendMailUseCase = get()) }

}