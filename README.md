# IMSLP-Video-Live-Streaming
Live video streaming extension for the largest online sheet music community [IMSLP.org](https://www.imslp.org)

IMSLP.org is the largest online sheet music community containing sheet music for most classical music pieces.
This project enabled musicians to provide livestreams of their interpretations of IMSLP music pieces via Youtube Live.
The project is currently not online anymore. At time of deployment it was fully functional and the documentation is provided.

### Embedded Youtube players

The web-application allowed users to create livestreams via Youtube which were directly referenced on the corresponding IMSLP page 
containing additional information about the played piece e.g. sheet music, composer, date of composition etc.
To do so, a new widget was added that contained all referenced (live) Youtube videos that could be watched and compared while following 
the sheet music displayed on the same IMSLP page.

![alt IMSLP-Embed](https://raw.githubusercontent.com/MrStonebreaker/IMSLP-Video-Live-Streaming/master/doc/IMSLP-Embed.png)

### Go live via Youtube and link performance with IMSLP.org

Besides guidance for the user on how to set up a livestream, the YoutubeAPI was implemented to carry out a large part of work to create a livestream for fast and easy user access. For the guidance and setup process a separated webpage was provided. The user interface was written in Java with the GWT (Google Web Toolkit). 

![alt GoLiveSection](https://raw.githubusercontent.com/MrStonebreaker/IMSLP-Video-Live-Streaming/master/doc/GoLiveSection.png)


### Implementation

The YoutubeAPI implementation to set up livestreams was written in Java. The client-server infrastructure was provided by GoogleApp Engine.
References of Youtube (live) videos were hold in a database. When a livestream was created the responsible Java servlet created an entry in the database. 
The added (html/css/javascript) widget on IMSLP.org accessed video references in the database via AJAX-calls. 
Three cron-jobs executed in regular intervals were implemented to keep the database clean f.e. by deleting outdated references.


![alt ApplicationOverview](https://raw.githubusercontent.com/MrStonebreaker/IMSLP-Video-Live-Streaming/master/doc/Overview.png)


### References

Full documentation can be found [here](https://raw.githubusercontent.com/MrStonebreaker/IMSLP-Video-Live-Streaming/master/doc/Bachelorarbeit_Thomas_Steinbrecher.pdf)
