class Particle {

  PVector velocity;
  float lifespan = 255;
  
  PShape part;
  float partSize;
  
  PVector gravity = new PVector(0,0.1);

  Particle() {
    partSize = random(10,60);
    part = createShape();
    part.beginShape(QUAD);
    part.noStroke();
    part.texture(sprite);
    part.normal(0, 0, 1);
    part.vertex(-partSize/2, -partSize/2, 0, 0);
    part.vertex(+partSize/2, -partSize/2, sprite.width, 0);
    part.vertex(+partSize/2, +partSize/2, sprite.width, sprite.height);
    part.vertex(-partSize/2, +partSize/2, 0, sprite.height);
    part.endShape();
    
    rebirth(width/2,height/2);
    lifespan = random(255);
  }

  PShape getShape() {
    return part;
  }
  
  void rebirth(float x, float y) {
    float a = random(TWO_PI);
    float speed = random(0.5,4);
    velocity = new PVector(cos(a), sin(a));
    velocity.mult(speed);
    lifespan = 255;   
    part.resetMatrix();
    part.translate(x, y); 
  }
  
  boolean isDead() {
    if (lifespan < 0) {
     return true;
    } else {
     return false;
    } 
  }
  

  public void update() {
    lifespan = lifespan - 1;
    velocity.add(gravity);
    
    part.setTint(color(255,lifespan));
    part.translate(velocity.x, velocity.y);
  }
}

class ParticleSystem {
  ArrayList<Particle> particles;

  PShape particleShape;

  ParticleSystem(int n) {
    particles = new ArrayList<Particle>();
    particleShape = createShape(PShape.GROUP);

    for (int i = 0; i < n; i++) {
      Particle p = new Particle();
      particles.add(p);
      particleShape.addChild(p.getShape());
    }
  }

  void update() {
    for (Particle p : particles) {
      p.update();
    }
  }

  void setEmitter(float x, float y) {
    for (Particle p : particles) {
      if (p.isDead()) {
        p.rebirth(x, y);
      }
    }
  }

  void display() {

    shape(particleShape);
  }
}


import http.requests.*;

ParticleSystem ps;
PImage sprite;  
int lastTimeCheck;
int timeIntervalFlag = 3000; 
int[] acc = new int[101];
int fem = 0;

void setup() {
    lastTimeCheck = millis();
    size(1024, 768, P2D);
    orientation(LANDSCAPE);
    sprite = loadImage("sprite.png");
    ps = new ParticleSystem(10);

  // Writing to the depth buffer is disabled to avoid rendering
  // artifacts due to the fact that the particles are semi-transparent
  // but not z-sorted.
  
    GetRequest get = new GetRequest("http://datamx.io/api/action/datastore_search?resource_id=a5350224-c42a-4aee-919c-b091afccb7d9&limit=100&q=Residencial");
    get.send();
    JSONObject response = parseJSONObject(get.getContent());
    JSONObject dos = response.getJSONObject("result");
    JSONArray boxes = dos.getJSONArray("records");
    for(int i=0;i<boxes.size();i++) {
      JSONObject box = boxes.getJSONObject(i);
      println(box.getString("Sexo"));
      if(box.getString("Sexo").equals("Femenino")){
        acc[i] = 1;
      } 
      else{
        acc[i] = 2;
      }
    }
    background(0);
    hint(DISABLE_DEPTH_MASK);
} 

void draw () {
  
  ps.update();
  ps.display();
  
   if ( millis() > lastTimeCheck + timeIntervalFlag && fem < 99) {
    lastTimeCheck = millis();
    if(acc[fem]==1){
      ps.setEmitter(600,300);
    }
    else{
      ps.setEmitter(100,300);
    }
    fem=fem+1;
  }
  
  fill(255);
  textSize(16);
  text("Frame rate: " + int(frameRate), 10, 20);
}
