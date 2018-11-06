package net.umatoma.torrentmediaapp.service;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import org.fourthline.cling.binding.xml.ServiceDescriptorBinder;
import org.fourthline.cling.binding.xml.UDA10DeviceDescriptorBinderImpl;
import org.fourthline.cling.binding.xml.UDA10ServiceDescriptorBinderImpl;

public class CustomAndroidUpnpService extends AndroidUpnpServiceImpl {
    @Override
    protected UpnpServiceConfiguration createConfiguration() {
        return new AndroidUpnpServiceConfiguration() {
            @Override
            public DeviceDescriptorBinder getDeviceDescriptorBinderUDA10() {
                return new UDA10DeviceDescriptorBinderImpl();
            }

            @Override
            public ServiceDescriptorBinder getServiceDescriptorBinderUDA10() {
                return new UDA10ServiceDescriptorBinderImpl();
            }
        };
    }
}
