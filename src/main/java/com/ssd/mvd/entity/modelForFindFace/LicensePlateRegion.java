package com.ssd.mvd.entity.modelForFindFace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicensePlateRegion{
	private Double confidence;
	private String name;
}