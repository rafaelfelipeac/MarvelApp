package com.rafaelfelipeac.githubrepositories.base

import junit.framework.TestCase

infix fun <T> T.equalTo(expected: T?): T = this.apply {
    TestCase.assertEquals(
        expected,
        this
    )
}
