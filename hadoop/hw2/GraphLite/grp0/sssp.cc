/**
 * @file sssp.cc
 * @author Json Lee
 */
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <limits.h>
#include "GraphLite.h"
#include <iostream>
#include <algorithm>

//#define EPS 1e-6
//#define VERTEX_CLASS_NAME(name) PageRankVertex##name
//#define VERTEX_CLASS_NAME(name) DirectedTriangleCount##name
#define VERTEX_CLASS_NAME(name) SSSP##name
using namespace std;

//int64_t v0_id;
unsigned long long v0_id;

class VERTEX_CLASS_NAME(InputFormatter): public InputFormatter {
public:
    int64_t getVertexNum() {
        unsigned long long n;
        sscanf(m_ptotal_vertex_line, "%lld", &n);
        m_total_vertex= n;
        return m_total_vertex;
    }
    int64_t getEdgeNum() {
        unsigned long long n;
        sscanf(m_ptotal_edge_line, "%lld", &n);
        m_total_edge= n;
        return m_total_edge;
    }
    int getVertexValueSize() {
        m_n_value_size = sizeof(int64_t);
        return m_n_value_size;
    }
    int getEdgeValueSize() {
        m_e_value_size = sizeof(int64_t);
        return m_e_value_size;
    }
    int getMessageValueSize() {
        m_m_value_size = sizeof(int64_t);
        return m_m_value_size;
    }
    void loadGraph() {
        unsigned long long last_vertex;
        unsigned long long from;
        unsigned long long to;
        unsigned long long weight = 0;
        
        int64_t value = 0;
        int outdegree = 0;
        
        const char *line= getEdgeLine();

        // Note: modify this if an edge weight is to be read
        //       modify the 'weight' variable

        sscanf(line, "%lld %lld %lld", &from, &to,&weight);
        addEdge(from, to, &weight);

        last_vertex = from;
        ++outdegree;
        for (int64_t i = 1; i < m_total_edge; ++i) {
            line= getEdgeLine();

            // Note: modify this if an edge weight is to be read
            //       modify the 'weight' variable

            //sscanf(line, "%lld %lld", &from, &to);
            sscanf(line, "%lld %lld %lld", &from, &to,&weight);
            if (last_vertex != from) {
                addVertex(last_vertex, &value, outdegree);
                last_vertex = from;
                outdegree = 1;
            } else {
                ++outdegree;
            }
            addEdge(from, to, &weight);
        }
        addVertex(last_vertex, &value, outdegree);
    }
};

class VERTEX_CLASS_NAME(OutputFormatter): public OutputFormatter {
public:
    void writeResult() {
        int64_t vid;
        int64_t value;
        char s[1024];

        for (ResultIterator r_iter; ! r_iter.done(); r_iter.next() ) {
            r_iter.getIdValue(vid, &value);
            int n = sprintf(s, "%lld: %lld\n", (unsigned long long)vid, (unsigned long long)value);
            writeNextResLine(s, n);
        }
    }
};

// An aggregator that records a int64_t value to compute distance
class VERTEX_CLASS_NAME(Aggregator): public Aggregator<int64_t> {
public:
    void init() {
        m_global = 0;
        m_local = 0;
    }
    void* getGlobal() {
        return &m_global;
    }
    void setGlobal(const void* p) {
        m_global = * (int64_t *)p;
    }
    void* getLocal() {
        return &m_local;
    }
    void merge(const void* p) {
        //m_global += * (double *)p;
        m_global = min(m_global,*(int64_t *)p);
    }
    void accumulate(const void* p) {
        //m_local += * (double *)p;
        m_local = min(m_local,*(int64_t *)p);
    }
};

class VERTEX_CLASS_NAME(): public Vertex <int64_t, int64_t, int64_t> {
public:
    void compute(MessageIterator* pmsgs) 
    {
        //double val;
        if (getSuperstep() == 0)//init
        {
           //val= 1.0;
            if(m_pme->m_v_id==v0_id)
            {
                *mutableValue()=0;
            }
            else
            {
                *mutableValue()=INT_MAX;
            }
        } 
        else 
        {
            int64_t min_value=getValue();
            for(;!pmsgs->done(); pmsgs->next())
            {
                min_value=min(min_value,pmsgs->getValue());
            }
            if(min_value==getValue())
            {
                voteToHalt();
                return;
            }
            *mutableValue() = min_value;
        }
        auto out_it=getOutEdgeIterator();
        for(;!out_it.done();out_it.next())
        {
            sendMessageTo(out_it.target(),/*min_value*/getValue()+out_it.getValue());
        }
    }
};

class VERTEX_CLASS_NAME(Graph): public Graph {
public:
    VERTEX_CLASS_NAME(Aggregator)* aggregator;

public:
    // argv[0]: PageRankVertex.so
    // argv[1]: <input path>
    // argv[2]: <output path>
    void init(int argc, char* argv[]) {

        setNumHosts(5);
        setHost(0, "localhost", 1411);
        setHost(1, "localhost", 1421);
        setHost(2, "localhost", 1431);
        setHost(3, "localhost", 1441);
        setHost(4, "localhost", 1451);

        if (argc < 4) {
           printf ("Usage: %s <input path> <output path>\n", argv[0]);
           exit(1);
        }

        m_pin_path = argv[1];
        m_pout_path = argv[2];

        //int64_t v0_id;
        sscanf(argv[3],"%lld",&v0_id);
        aggregator = new VERTEX_CLASS_NAME(Aggregator)[1];
        regNumAggr(1);
        regAggr(0, &aggregator[0]);
    }

    void term() {
        cout<<"+++++++++++++++++++++++++++++++"<<endl;
        delete[] aggregator;
    }
};

/* STOP: do not change the code below. */
extern "C" Graph* create_graph() {
    Graph* pgraph = new VERTEX_CLASS_NAME(Graph);

    pgraph->m_pin_formatter = new VERTEX_CLASS_NAME(InputFormatter);
    pgraph->m_pout_formatter = new VERTEX_CLASS_NAME(OutputFormatter);
    pgraph->m_pver_base = new VERTEX_CLASS_NAME();

    return pgraph;
}

extern "C" void destroy_graph(Graph* pobject) {
    delete ( VERTEX_CLASS_NAME()* )(pobject->m_pver_base);
    delete ( VERTEX_CLASS_NAME(OutputFormatter)* )(pobject->m_pout_formatter);
    delete ( VERTEX_CLASS_NAME(InputFormatter)* )(pobject->m_pin_formatter);
    delete ( VERTEX_CLASS_NAME(Graph)* )pobject;
}
