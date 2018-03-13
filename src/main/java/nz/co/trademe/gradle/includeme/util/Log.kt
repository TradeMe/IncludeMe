package nz.co.trademe.gradle.includeme.util

import nz.co.trademe.gradle.includeme.IncludeMePlugin
import org.gradle.api.logging.Logging
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger(IncludeMePlugin::class.simpleName)

fun log(message: String) {
    logger.info(Logging.LIFECYCLE, "[IncludeMe] $message")
}

fun logI(message: String) {
    logger.info("[IncludeMe] $message")
}

fun logD(message: String) {
    logger.debug("[IncludeMe] $message")
}