package com.document.render.pdf

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PdfQuality
 * Author: Victor
 * Date: 2023/09/28 11:15
 * Description: 
 * -----------------------------------------------------------------
 */

enum class PdfQuality(val ratio: Int) {
    FAST(1),
    NORMAL(2),
    ENHANCED(3)
}