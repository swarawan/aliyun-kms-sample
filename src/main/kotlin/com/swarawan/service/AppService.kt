package com.swarawan.service

import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.http.FormatType
import com.aliyuncs.http.MethodType
import com.aliyuncs.http.ProtocolType
import com.aliyuncs.kms.model.v20160120.*
import com.aliyuncs.profile.DefaultProfile
import com.swarawan.AppConfiguration

class AppService {

	private val kmsClient: DefaultAcsClient by lazy { defaultClient() }

	fun findAllKeys(): ListKeysResponse {
		val request = ListKeysRequest().apply {
			sysProtocol = ProtocolType.HTTPS
			acceptFormat = FormatType.JSON
			sysMethod = MethodType.POST
			pageNumber = 1
			pageSize = 100
		}
		return kmsClient.getAcsResponse(request)
	}

	fun findDetails(appKeyId: String): DescribeKeyResponse {
		val request = DescribeKeyRequest().apply {
			sysProtocol = ProtocolType.HTTPS
			acceptFormat = FormatType.JSON
			sysMethod = MethodType.POST
			keyId = appKeyId
		}
		return kmsClient.getAcsResponse(request)
	}

	fun doEncrypt(text: String, appKeyId: String): EncryptResponse {
		val request = EncryptRequest().apply {
			sysProtocol = ProtocolType.HTTPS
			acceptFormat = FormatType.JSON
			sysMethod = MethodType.POST
			keyId = appKeyId
			plaintext = text
		}
		return kmsClient.getAcsResponse(request)
	}

	fun doDecrypt(cipher: String): DecryptResponse {
		val request = DecryptRequest().apply {
			sysProtocol = ProtocolType.HTTPS
			acceptFormat = FormatType.JSON
			sysMethod = MethodType.POST
			ciphertextBlob = cipher
		}
		return kmsClient.getAcsResponse(request)
	}

	/**
	 * ======================================================
	 * PRIVATE FUNCTIONS
	 * ======================================================
	 */

	private fun defaultClient(): DefaultAcsClient {
		val profile = DefaultProfile.getProfile(
			AppConfiguration.REGION_ID,
			AppConfiguration.ACCESS_KEY,
			AppConfiguration.ACCESS_SECRET
		)
		return DefaultAcsClient(profile)
	}
}