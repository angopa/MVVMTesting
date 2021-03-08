package com.andgp.mvvmtesting

import android.app.Application

/**
 *  Created by Andres Gonzalez on 3/3/21.
 *  Copyright (c) 2020 City Electric Supply. All rights reserved.
 */
open class BaseApplication(
    val isDebug: Boolean = false
) : Application()