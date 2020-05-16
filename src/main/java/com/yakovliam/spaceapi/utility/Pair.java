package com.yakovliam.spaceapi.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class Pair<L, R> {
    @Getter
    @Setter
    L left;
    @Getter
    @Setter
    R right;
}