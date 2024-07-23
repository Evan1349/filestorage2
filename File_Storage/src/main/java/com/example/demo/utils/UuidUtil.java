package com.example.demo.utils;

import java.util.UUID;

public class UuidUtil {
	
	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "");
	}

}
