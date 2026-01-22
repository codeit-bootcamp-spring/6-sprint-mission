PR Notes

InMemoryJwtRegistry limitations in a distributed environment:
- JWT state is stored per-instance, so logout/rotation on one node does not invalidate tokens on others.
- Active token limits are enforced only locally, allowing users to exceed limits when hitting multiple nodes.
- Registry data is lost on restart/scale events, causing inconsistent validation and user online state.

WebSocket/SSE limitations in a distributed environment:
- Connection and emitter state is kept in-memory on a single node, so events are only delivered to clients connected to the same instance.
- Reconnects can land on a different node and miss local-only message history.
