package com.ssd.mvd.entity.modelForPassport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Make{
	private String name;
	private Double confidence;
}