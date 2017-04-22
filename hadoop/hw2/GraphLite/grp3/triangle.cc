/**
 * @file triangle.cc
 * @author Json Lee
 *Directed triangle count, by modifying the page rank example
 *Obviously, we can know that the number of in or out triangles equals
 *the number of through triangles, so we can simply count in & cycle triangles or out & cycle triangles, since it is easy to determine in or out triangles.
 *Here we take in & cycle triangles for an example. Obviously, we can know the fact that:
 *for one node, if the previous of its previous node is its successive node, then they form a cycle triangle;
 *while if the previous of its previous node is its successive node is its previous node, then they form a in triangle.
 */

#include <stdio.h>
#include <string.h>
#include <math.h>
#include <vector>
#include <set>
#include "GraphLite.h"
#include <iostream>

//#define VERTEX_CLASS_NAME(name) PageRankVertex##name
#define VERTEX_CLASS_NAME(name) DirectedTriangleCount##name
using namespace std;

//#define EPS 1e-6
//Vertex Value Type,since in=out=through,so we only need to compute in and cycle
typedef struct VertexType
{
    int in_num;
    int cycle_num;
}VertexType;
//msg value type, store the source id and dest id
typedef struct MsgType
{
    int64_t src_id,dest_id;
}MsgType;

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
        m_n_value_size = sizeof(VertexType);
        return m_n_value_size;
    }
    int getEdgeValueSize() {
        m_e_value_size = sizeof(double);
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
        double weight = 0;
        
        //double value = 1;
        VertexType value;
        int outdegree = 0;
        
        const char *line= getEdgeLine();

        // Note: modify this if an edge weight is to be read
        //       modify the 'weight' variable

        sscanf(line, "%lld %lld", &from, &to);
        addEdge(from, to, &weight);

        last_vertex = from;
        ++outdegree;
        for (int64_t i = 1; i < m_total_edge; ++i) {
            line= getEdgeLine();

            // Note: modify this if an edge weight is to be read
            //       modify the 'weight' variable

            sscanf(line, "%lld %lld", &from, &to);
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
        //double value;
        VertexType value;
        char s[1024];

        // for (ResultIterator r_iter; ! r_iter.done(); r_iter.next() ) {
        //     r_iter.getIdValue(vid, &value);
        //     int n = sprintf(s, "%lld: %f\n", (unsigned long long)vid, value);
        //     writeNextResLine(s, n);
        // }
        ResultIterator r_iter;
        r_iter.getIdValue(vid, &value);
        int n = sprintf(s, "in: %d\nout: %d\nthrough: %d\ncycle: %d\n", value.in_num,value.in_num,value.in_num,value.cycle_num);
        writeNextResLine(s, n);
    }
};

// An aggregator that records a VertexType value to compute triangle's number
class VERTEX_CLASS_NAME(Aggregator): public Aggregator<VertexType> {
public:
    void init() {
        // m_global = 0;
        // m_local = 0;
        memset(&m_global,0,sizeof(VertexType));
        memset(&m_local,0,sizeof(VertexType));
    }
    void* getGlobal() {
        return &m_global;
    }
    void setGlobal(const void* p) {
        //m_global = * (double *)p;
        //m_global = * (VertexType *)p;
        memmove(&m_global,p,sizeof(VertexType));
    }
    void* getLocal() {
        return &m_local;
    }
    void merge(const void* p) {
        //m_global += * (double *)p;
        m_global.in_num+=((VertexType *)p)->in_num;
        m_global.cycle_num+=((VertexType *)p)->cycle_num;
    }
    void accumulate(const void* p) {
        //m_local += * (double *)p;
        m_local.in_num+=((VertexType *)p)->in_num;
        m_local.cycle_num+=((VertexType *)p)->cycle_num;
    }
};

class VERTEX_CLASS_NAME(): public Vertex <VertexType, double, int64_t/*MsgType*/> {
public:
    void compute(MessageIterator* pmsgs) {
        //double val;
        if (getSuperstep() == 0) {
            //val= 1.0;
            //send id msg to all out neighbours
            sendMessageToAllNeighbors(m_pme->m_v_id);
        }
        else if(getSuperstep()==1)
        {
            for ( ; ! pmsgs->done(); pmsgs->next() ) {
                //send in neighbour msg to all neighbours 
                sendMessageToAllNeighbors(pmsgs->getValue());
                //since msg will be cleaned after one superstep, we need to send it again to store the in neighbour msg
                sendMessageTo(m_pme->m_v_id,pmsgs->getValue());
            }
        }
        else if(getSuperstep()==2)
        {
            //for one node, if the previous of its previous node is its successive node, then they form a cycle triangle;
            //while if the previous of its previous node is its successive node is its previous node, then they form a in triangle.
            vector<int64_t> prev_prev;
            set<int64_t> prev;
            for(; !pmsgs->done(); pmsgs->next())
            {
                //current msg sender's id equals that of mine, then it must be my previous node
                //else it must be previous of my previous node
                if(((Msg *)pmsgs->getCurrent())->s_id==m_pme->m_v_id)
                {
                    prev.insert(pmsgs->getValue());
                }
                else
                {
                    prev_prev.push_back(pmsgs->getValue());
                }
            }
            //get the out edge of mine, then the target of the out edge must be my successive node 
            set<int64_t> succ;
            auto succ_it=getOutEdgeIterator();
            for(;!succ_it.done();succ_it.next())
            {
                succ.insert(succ_it.target());
            }
            VertexType acc={0,0};//local accumulate
            auto prev_prev_it=prev_prev.begin(),prev_prev_ie=prev_prev.end();
            for(;prev_prev_it!=prev_prev_ie;++prev_prev_it)
            {
                //if the previous of my previous node is my previous node, then they form a in triangle
                if(prev.find(*prev_prev_it)!=prev.end())
                {
                    ++acc.in_num;
                }
                //if the previous of my previous node is my successive node, then they form a cycle triangle
                if (succ.find(*prev_prev_it)!=succ.end())
                {
                    ++acc.cycle_num;
                }
            }
            accumulateAggr(0,&acc);
        }
        else if(getSuperstep()==3)
        {
            * mutableValue() = * (VertexType *)getAggrGlobal(0);
            voteToHalt(); 
            return;
        }
    }
};

class VERTEX_CLASS_NAME(Graph): public Graph {
public:
    VERTEX_CLASS_NAME(Aggregator)* aggregator;

public:
    // argv[0]: triangle.so
    // argv[1]: <input path>
    // argv[2]: <output path>
    void init(int argc, char* argv[]) {

        setNumHosts(5);
        setHost(0, "localhost", 1411);
        setHost(1, "localhost", 1421);
        setHost(2, "localhost", 1431);
        setHost(3, "localhost", 1441);
        setHost(4, "localhost", 1451);

        if (argc < 3) {
           printf ("Usage: %s <input path> <output path>\n", argv[0]);
           exit(1);
        }

        m_pin_path = argv[1];
        m_pout_path = argv[2];

        aggregator = new VERTEX_CLASS_NAME(Aggregator)[1];
        regNumAggr(1);
        regAggr(0, &aggregator[0]);
    }

    void term() {
        //cout<<"++++++++++++++++++++++++++++++"<<endl;
        VertexType result=*((VertexType*)(aggregator[0].getGlobal()));
        cout<<"in: "<<result.in_num<<"\nout: "<<result.in_num<<"\nthrough: "<<result.in_num<<"\ncycle: "<<result.cycle_num<<endl;
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
