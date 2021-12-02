package me.saharnooby.qoi;

import lombok.NonNull;

import java.io.IOException;

/**
 * This exception is thrown when decoder detects invalid data in the input stream.
 */
public final class InvalidQOIStreamException extends IOException {

	InvalidQOIStreamException(@NonNull String message) {
		super(message);
	}

}
