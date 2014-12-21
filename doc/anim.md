# Character Animation

A character is composed of a skeleton. Skeleton is a set of bones,
each with a parent bone except for the root bone.

An animation defines for a point t a rotation of each bone and a
translation of the model overall.

So consider a skeleton with two bones

  [ { :id "root", :parent nil, 
      :v 1.0, 
    { :id "child", :parent "root", 
      :v 1.0,  ]

So each bone is represented by a vector v giving it length and initial
orientation at time t=0.

Lets consider a function q: (t, bone) -> quaternion which gives the
rotation of bones at times t. There is also a function p: (t, bone) ->
vector which gives the translation of the head of the bone. 

Each bone also has a mesh associated with it. A mesh is a sequence of
triangles with vertex data.

We are interested in the transformation of points in the space of the
bone.

Lets consider a point x that is relative to the head of the bone. Then
we first rotate x by q then translate by p. Lets use o to denote an
operator formed from q or p.

 x' = o(p) o(q) x

Lets denote the transformation of a bone b at time t by o(bone).

 p(bone, t) = o(parent(bone), t) bone.v

Now we wish to perform these translations within the vertex shader. We
can have the animation stored in an array

  q[bone, t]

But we need to follow to the root of the tree. We could have

  parent[bone]

and loop in the shader until the parent is 0. 

For animation we might late like the bone to have a displacement as
well as a rotation.

t would be passed in as a uniform to all verticies. bone would be a
vertex attribute and parent would be a uniform.

In the shader we have a linked list of transformations for each node.







  