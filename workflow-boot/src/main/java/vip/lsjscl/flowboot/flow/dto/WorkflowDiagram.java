package vip.lsjscl.flowboot.flow.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * 用于映射流程图 JSON 对象，对应 data.json 文件
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowDiagram {
    private List<Node> nodes;
    private List<Edge> edges;

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Node {
        private String id;
        private String shape;
        private int x;
        private int y;
        private int width;
        private int height;
        private String label;
        private String type;
        private Map<String, Object> data; // activity 节点下的 data 可按需要进行二次解析

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getShape() {
            return shape;
        }

        public void setShape(String shape) {
            this.shape = shape;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Edge {
        private String source;
        private String target;
        private List<Object> vertices; // 根据实际情况可以自定义类型
        private Map<String, Object> data; // transition 节点可能包含 data 信息
        private Router router;
        private Connector connector;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public List<Object> getVertices() {
            return vertices;
        }

        public void setVertices(List<Object> vertices) {
            this.vertices = vertices;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }

        public Router getRouter() {
            return router;
        }

        public void setRouter(Router router) {
            this.router = router;
        }

        public Connector getConnector() {
            return connector;
        }

        public void setConnector(Connector connector) {
            this.connector = connector;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Router {
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Connector {
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
} 