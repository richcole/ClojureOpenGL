(ns deforma.physics
  (:use deforma.geom deforma.vector)
  (:import game.math.Vector)
  (:import game.math.Quaternion)
  (:import com.bulletphysics.collision.broadphase.BroadphaseInterface)
  (:import com.bulletphysics.collision.broadphase.DbvtBroadphase)
  (:import com.bulletphysics.collision.dispatch.CollisionDispatcher)
  (:import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration)
  (:import com.bulletphysics.collision.shapes.BoxShape)
  (:import com.bulletphysics.collision.shapes.CollisionShape)
  (:import com.bulletphysics.collision.shapes.StaticPlaneShape)
  (:import com.bulletphysics.collision.shapes.SphereShape)
  (:import com.bulletphysics.dynamics.DiscreteDynamicsWorld)
  (:import com.bulletphysics.dynamics.RigidBody)
  (:import com.bulletphysics.dynamics.RigidBodyConstructionInfo)
  (:import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver)
  (:import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver)
  (:import com.bulletphysics.linearmath.DefaultMotionState)
  (:import com.bulletphysics.linearmath.Transform)
  (:import javax.vecmath.Vector3f)
  (:import javax.vecmath.Quat4f)
  (:import javax.vecmath.Matrix4f)
  (:gen-class))


(deftype Entity [^Vector v ^Quaternion a ^Vector p ^Quaternion o ^Double m]
  ; velocity angular_velocity position orientation mass
)

(defn move ^Entity [^Entity e ^Double dt]
  (let [p (vplus (.p e) (svtimes dt (.v e)))
        o (qtimes (sqtimes (.a e)) (.o e))]
    (Entity. (.v e) (.a e) p o (.m e))))

(defn push ^Entity [^Entity e ^Vector p ^Vector f Double dt]
  "A point force in a direction f at a position p"
  (let [dfp (vproject (vnormalize (vminus (.p e) p)) f)
        dfa (svtimes (/ dt (.m e)) (vminus f dfp))
        dv  (svtimes (/ dt (.m e)) dfp)
        da  (Quaternion. (.x dfa) (.y dfa) (.z dfa) 0)
        v   (vplus (.v e) dv)
        a   (qtimes (.a e) da)
       ]
    (Entity. v a (.p e) (.o e) (.m e))))

(defn hello-world []
  (let [broadphase (DbvtBroadphase.)
        collisionConfiguration (DefaultCollisionConfiguration.)
        dispatcher (CollisionDispatcher. collisionConfiguration)
        solver (SequentialImpulseConstraintSolver.)
        dynamicsWorld (DiscreteDynamicsWorld.
               dispatcher broadphase solver collisionConfiguration)
        _ (.setGravity dynamicsWorld (Vector3f. 0 -10 0))

        groundShape (StaticPlaneShape. (Vector3f. 0 1 0) 1)
        fallShape (SphereShape. 1)

        groundMotionState (DefaultMotionState. 
                            (Transform. 
                             (Matrix4f. 
                              (Quat4f. 0 0 0 1)
                              (Vector3f. 0 -1 0)
                              1.0)))

        groundRigidBodyCI (RigidBodyConstructionInfo. 
                           0 groundMotionState groundShape (Vector3f. 0 0 0))
        groundRigidBody (RigidBody. groundRigidBodyCI)
        _ (.addRigidBody dynamicsWorld groundRigidBody)

        fallMotionState (DefaultMotionState. 
                          (Transform. 
                           (Matrix4f. (Quat4f. 0 0 0 1) (Vector3f. 0 50 0) 1.0)))
        
        mass 1
        fallInertia (Vector3f. 0 0 0)
        _ (.calculateLocalInertia fallShape mass fallInertia)

        fallRigidBodyCI (RigidBodyConstructionInfo. mass fallMotionState fallShape fallInertia)
        fallRigidBody (RigidBody. fallRigidBodyCI)

        _ (.addRigidBody dynamicsWorld fallRigidBody)
        ]
    (doseq [i (range 300)]
      (.stepSimulation dynamicsWorld (/ 1.0 60) 10)
      (let [trans (Transform.)]
        (-> fallRigidBody (.getMotionState) (.getWorldTransform trans))
        (println "sphere height: " (-> trans (.origin) (.y)))))
))

(hello-world)

    
