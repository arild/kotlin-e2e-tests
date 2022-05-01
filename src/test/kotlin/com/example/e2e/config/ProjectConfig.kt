package com.example.e2e.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.extensions.spring.SpringExtension

class ProjectConfig : AbstractProjectConfig() {

    override fun extensions() = listOf(SpringExtension)

    override val isolationMode = IsolationMode.InstancePerTest
}
