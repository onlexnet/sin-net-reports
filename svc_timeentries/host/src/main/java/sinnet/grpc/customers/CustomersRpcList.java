package sinnet.grpc.customers;

import sinnet.grpc.mapping.RpcQueryHandler;

interface CustomersRpcList extends RpcQueryHandler<ListRequest, ListReply> { }
