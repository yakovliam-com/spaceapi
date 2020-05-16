package com.yakovliam.spaceapi.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class Trio<L, M, R> {
    @Getter @Setter
    L left;
    @Getter @Setter
    R right;
    @Getter @Setter
    M mid;
}
