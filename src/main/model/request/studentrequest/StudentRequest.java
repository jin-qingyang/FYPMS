package main.model.request.studentrequest;

import main.model.request.Request;

public abstract class StudentRequest extends Request {
    /**
     * The constructor of the request.
     *
     * @param requestID
     */
    public StudentRequest(String requestID) {
        super(requestID);
    }

    public StudentRequest() {
        super();
    }
    // TODO: implement this class
}
