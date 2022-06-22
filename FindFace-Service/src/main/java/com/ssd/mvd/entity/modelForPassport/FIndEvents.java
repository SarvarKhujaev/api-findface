package com.ssd.mvd.entity.modelForPassport;

import com.ssd.mvd.entity.modelForFindFace.PreferenceItem;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FIndEvents {
	private List<PreferenceItem> preferences;
}