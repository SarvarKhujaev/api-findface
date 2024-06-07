package com.ssd.mvd.entity.modelForPassport;

import java.util.List;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public final class Last {
	private List< Integer > bbox;
	private String timestamp;
	private double quality;
}