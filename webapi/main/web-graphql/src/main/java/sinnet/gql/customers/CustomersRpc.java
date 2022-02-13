package sinnet.gql.customers;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.grpc.customers.CustomersGrpc.CustomersImplBase;
import sinnet.grpc.customers.GetReply;
import sinnet.grpc.customers.GetRequest;
import sinnet.grpc.customers.ListReply;
import sinnet.grpc.customers.ListRequest;
import sinnet.grpc.customers.RemoveReply;
import sinnet.grpc.customers.RemoveRequest;
import sinnet.grpc.customers.ReserveReply;
import sinnet.grpc.customers.ReserveRequest;

@Component
@RequiredArgsConstructor
public class CustomersRpc extends CustomersImplBase {
    
    private final CustomersRpcList list;
    private final CustomersRpcRemove remove;
    private final CustomersRpcGet get;

    @Override
    public void list(ListRequest request, StreamObserver<ListReply> responseObserver) {
        list.query(request, responseObserver);
    }

    @Override
    public void remove(RemoveRequest request, StreamObserver<RemoveReply> responseObserver) {
        remove.command(request, responseObserver);
    }

    @Override
    public void get(GetRequest request, StreamObserver<GetReply> responseObserver) {
        get.query(request, responseObserver);
    }

    @Override
    public void reserve(ReserveRequest request, StreamObserver<ReserveReply> responseObserver) {
        // TODO Auto-generated method stub
        super.reserve(request, responseObserver);
    }

}
