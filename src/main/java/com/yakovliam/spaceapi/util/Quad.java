package com.yakovliam.spaceapi.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class Quad<A, B, C, D> {
    @Getter
    @Setter
    A a;
    @Getter
    @Setter
    B b;
    @Getter
    @Setter
    C c;
    @Getter
    @Setter
    D d;
}