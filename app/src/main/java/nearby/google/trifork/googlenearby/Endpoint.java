package nearby.google.trifork.googlenearby;

public class Endpoint {

    public String endpointId;
    public String deviceId;
    public String serviceId;
    public String endpointName;

    public Endpoint(String endpointId, String deviceId, String serviceId, String endpointName) {
        this.endpointId = endpointId;
        this.deviceId = deviceId;
        this.serviceId = serviceId;
        this.endpointName = endpointName;
    }
}