package de.fholzstein.mb.linux.mod

import io.kotlintest.specs.StringSpec

class ApplicationKtTest: StringSpec() {
    init {
        "Main should run successfully" {
            de.fholzstein.mb.linux.mod.main(arrayOf())
        }
    }
}