﻿using Hirs.Pb;
using System;
using System.Collections.Generic;
using System.Text;

namespace hirs {
    public interface IHirsDeviceInfoCollector {
        DeviceInfo CollectDeviceInfo(string address);
    }
}
