package com.swarawan.controller

import com.aliyuncs.exceptions.ClientException
import com.aliyuncs.kms.model.v20160120.DecryptResponse
import com.aliyuncs.kms.model.v20160120.DescribeKeyResponse
import com.aliyuncs.kms.model.v20160120.EncryptResponse
import com.aliyuncs.kms.model.v20160120.ListKeysResponse
import com.swarawan.service.AppService
import java.util.*
import kotlin.system.exitProcess

class AppController {

	companion object {
		private const val MENU_ID_FIND_ALL_KEY = "1"
		private const val MENU_ID_KEY_DETAILS = "2"
		private const val MENU_ID_ENCRYPT = "3"
		private const val MENU_ID_DECRYPT = "4"
	}

	private val scanner = Scanner(System.`in`).useDelimiter("\n")
	private val service = AppService()
	private val menu = arrayOf(
		MENU_ID_FIND_ALL_KEY to "Find All Key",
		MENU_ID_KEY_DETAILS to "Find Key Details",
		MENU_ID_ENCRYPT to "Encrypt",
		MENU_ID_DECRYPT to "Decrypt"
	)

	init {
		println("======= ALIYUN KMS SAMPLE - APP STARTED =======")
		runApp()
	}

	private fun runApp() {
		println("Please select the menu")
		menu.forEach {
			println("${it.first}. ${it.second}")
		}
		print("You choose (1 - ${menu.size}): ")

		when (scanner.next()) {
			MENU_ID_FIND_ALL_KEY -> doFindAllKeys()
			MENU_ID_KEY_DETAILS -> doFindKeyDetails()
			MENU_ID_ENCRYPT -> doEncryption()
			MENU_ID_DECRYPT -> doDecryption()
			else -> terminateApp("Wrong input. Run application to start")
		}
	}

	/**
	 * ======================================================
	 * PRIVATE FUNCTIONS
	 * ======================================================
	 */

	private fun doFindAllKeys() {
		try {
			val response: ListKeysResponse = service.findAllKeys()
			println("REQUEST ID : ${response.requestId}")
			response.keys.forEachIndexed { index, key ->
				println("KEY ${index + 1}: ${key.keyId}")
			}
		} catch (ex: ClientException) {
			printError(ex)
		} finally {
			println("======================================================")
			runApp()
		}
	}

	private fun doFindKeyDetails() {
		print("Your key: ")
		val keyId = scanner.next()
		if (keyId.isNullOrEmpty()) {
			terminateApp("cannot proceed empty keyId")
		}

		try {
			val response: DescribeKeyResponse = service.findDetails(keyId)
			println("REQUEST ID : ${response.requestId}")
			println("DESCRIPTION : ${response.keyMetadata.description}")
			println("STATE : ${response.keyMetadata.keyState}")
			println("USAGE : ${response.keyMetadata.keyUsage}")
		} catch (ex: ClientException) {
			printError(ex)
		} finally {
			println("======================================================")
			runApp()
		}
	}

	private fun doEncryption() {
		print("Type something: ")
		val plainText = scanner.next()
		if (plainText.isNullOrEmpty()) {
			terminateApp("cannot proceed empty value")
		}

		print("Your key: ")
		val keyId = scanner.next()
		if (keyId.isNullOrEmpty()) {
			terminateApp("cannot proceed empty keyId")
		}

		try {
			val response: EncryptResponse = service.doEncrypt(plainText, keyId)
			println("REQUEST ID : ${response.requestId}")
			println("PLAIN : $plainText")
			println("CIPHER : ${response.ciphertextBlob}")
		} catch (ex: ClientException) {
			println("======= ENCRYPTION FAILED =======")
			printError(ex)
		} finally {
			println("======================================================")
			runApp()
		}
	}

	private fun doDecryption() {
		print("Your cipher: ")
		val cipherText = scanner.next()
		if (cipherText.isNullOrEmpty()) {
			terminateApp("cannot proceed empty value")
		}

		try {
			val response: DecryptResponse = service.doDecrypt(cipherText)
			println("REQUEST ID : ${response.requestId}")
			println("CIPHER : ${response.keyId}")
			println("PLAIN : ${response.plaintext}")
		} catch (ex: ClientException) {
			println("======= DECRYPTION FAILED =======")
			printError(ex)
		} finally {
			println("======================================================")
			runApp()
		}
	}

	private fun terminateApp(reason: String = "") {
		if (reason.isNotEmpty()) {
			println("Terminate app with reason: $reason")
		}

		println("======= ALIYUN KMS SAMPLE - APP TERMINATED =======")
		exitProcess(0)
	}

	private fun printError(ex: ClientException) {
		println("CODE : ${ex.errCode}")
		println("MESSAGE : ${ex.errMsg}")
		terminateApp()
	}
}