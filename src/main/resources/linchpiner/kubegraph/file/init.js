var nodes = new vis.DataSet(_nodes);
var edges = new vis.DataSet(_edges);

var container = document.getElementById('kubegraph');
var data = {
    nodes: nodes,
    edges: edges
};
var options = {
    autoResize: true,
    layout: {
        improvedLayout: true,
        /*
        hierarchical: {
            enabled: true,
            sortMethod: 'directed',
        }
        */
    },
    groups: {
        node: {
            shape: 'icon',
            icon: {
                face: 'FontAwesome',
                code: '\uf233',
                size: 50,
                color: '#3366ff'
            }
        },
        replicationcontroller: {
            shape: 'icon',
            icon: {
                face: 'FontAwesome',
                code: '\uf0c5',
                size: 50,
                color: '#3366ff'
            }
        },
        deployment: {
            shape: 'icon',
            icon: {
                face: 'FontAwesome',
                code: '\uf085',
                size: 50,
                color: '#3366ff'
            }
        },
        pod: {
            shape: 'icon',
            icon: {
                face: 'FontAwesome',
                code: '\uf1b2',
                size: 50,
                color: '#ffcc00'
            }
        },
        'pod-running': {
            shape: 'icon',
            icon: {
                face: 'FontAwesome',
                code: '\uf1b2',
                size: 50,
                color: '#004d00'
            }
        },                    
        replicaset: {
            shape: 'icon',
            icon: {
                face: 'FontAwesome',
                code: '\uf013',
                size: 50,
                color: '#3366ff'
            }
        },
        service: {
            shape: 'icon',
            icon: {
                face: 'FontAwesome',
                code: '\uf0c2',
                size: 50,
                color: '#3366ff'
            }
        },
    }
};

var network = new vis.Network(container, data, options);
