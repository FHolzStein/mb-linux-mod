package de.fholzstein.mb.linux.mod

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class ApplicationKtTest: StringSpec() {
    init {
        "Main should run successfully" {
            de.fholzstein.mb.linux.mod.main(arrayOf())
            1 shouldBe  2
        }
    }
}