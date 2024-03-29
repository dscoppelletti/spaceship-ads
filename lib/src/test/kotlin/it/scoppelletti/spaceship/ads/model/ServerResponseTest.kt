package it.scoppelletti.spaceship.ads.model

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import it.scoppelletti.spaceship.io.closeQuietly
import it.scoppelletti.spaceship.types.joinLines
import okio.BufferedSource
import okio.Source
import okio.buffer
import okio.source
import java.io.InputStream
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ServerResponseTest {

    private lateinit var moshi: Moshi
    private lateinit var adapter: JsonAdapter<ServerResponse>

    @BeforeTest
    fun setUp() {
        moshi = Moshi.Builder().build()
        adapter = moshi.adapter(ServerResponse::class.java)
    }

    @Test
    fun load() {
        val resp: ServerResponse?
        val publisher: AdNetworkLookupResponse?
        var company: AdProvider?
        var stream: InputStream? = null
        var source: Source? = null
        var reader: BufferedSource? = null

        try {
            stream = javaClass.classLoader?.getResourceAsStream(
                    "ServerResponse.json")
            assertNotNull(stream, "ServerResponse.json not found.")

            source = stream.source()
            stream = null

            reader = source.buffer()
            source = null

            resp = adapter.fromJson(reader)
        } finally {
            stream?.closeQuietly()
            source?.closeQuietly()
            reader?.closeQuietly()
        }

        assertNotNull(resp, "Response not loaded.")

        assertEquals(1, resp.adNetworkLookupResponses.size,
                "The number of publishers is not one.")

        publisher = resp.adNetworkLookupResponses[0]
        assertEquals("ca-app-pub-NNNNNNNNNNNNNNNN", publisher.networkId,
                "Publisher ID differs.")
        assertFalse(publisher.lookupFailed, "Lookup is failed.")
        assertFalse(publisher.notFound, "Publisher not found.")
        assertTrue(publisher.isNPA, """Publisher has not configured any non
                |personalized Ad providers.""".trimMargin().joinLines())

        assertEquals(3, resp.companies.size,
                "The number of providers is not 3.")

        company = resp.companies[0]
        assertEquals("1765", company.companyId, "Provider 1: ID differs.")
        assertEquals("Aarki", company.name, "Provider 1: name differs.")
        assertEquals("http://corp.aarki.com/privacy", company.policyUrl,
                "Provider 1: policy URL differs")

        company = resp.companies[1]
        assertEquals("236", company.companyId, "Provider 2: ID differs.")
        assertEquals("Adacado", company.name, "Provider 2: name differs.")
        assertEquals("https://www.adacado.com/privacy-policy-april-25-2018/",
                company.policyUrl, "Provider 2: policy URL differs")

        company = resp.companies[2]
        assertEquals("15", company.companyId, "Provider 3: ID differs.")
        assertEquals("Adara Media", company.name, "Provider 3: name differs.")
        assertEquals("https://adara.com/2018/04/10/adara-gdpr-faq/",
                company.policyUrl, "Provider 3: policy URL differs")

        assertTrue(resp.isRequestLocationInEeaOrUnknown,
                "The publisher is not in EEA.")
    }
}
