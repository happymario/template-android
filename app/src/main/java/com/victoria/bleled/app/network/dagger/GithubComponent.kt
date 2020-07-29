package com.victoria.bleled.app.network.dagger

import com.victoria.bleled.app.network.search.DetailListFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [GithubModule::class])
interface GithubComponent {
    fun inject(fragment: DetailListFragment)
}