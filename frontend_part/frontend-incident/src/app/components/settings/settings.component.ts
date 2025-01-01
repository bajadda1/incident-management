import {Component, OnInit} from '@angular/core';
import {SectorService} from '../../services/sector-service/sector.service';
import {SectorDTO} from '../../models/sector';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {TypeService} from '../../services/type-service/type.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css',
  standalone: false
})
export class SettingsComponent implements OnInit {

  sectors: SectorDTO[] = []
  sectorFormGroup!: FormGroup
  typeFormGroup!: FormGroup

  errorMessage = '';

  constructor(private sectorService: SectorService,
              private typeService: TypeService,
              private fb: FormBuilder) {
  }


  ngOnInit(): void {
    this.getSectors();
    this.sectorFormGroup = this.fb.group({
        id: this.fb.control(null),
        name: this.fb.control("", [Validators.required])
      }
    );

    this.typeFormGroup = this.fb.group({
        id: this.fb.control(null),
        name: this.fb.control("", [Validators.required]),
        sectorId: this.fb.control(null, [Validators.required])
      }
    );
  }

  getSectors() {
    this.sectorService.getSectors().subscribe(
      (sectors) => {

        this.sectors = sectors;
      },
      (err) => {
        console.log(err);
      }
    )
  }

  typeSubmit() {

    if (this.typeFormGroup.invalid) {
      console.log(this.typeFormGroup.errors);
      return;
    }

    const {id, name, sectorId} = this.typeFormGroup.value;

    console.log(this.typeFormGroup.value);
    console.log("sector Id :" + sectorId);

    this.typeService.addType(sectorId, {id, name, sectorDTO: null}).subscribe(
      (type) => {
        console.log(type);
      },
      (err) => {
        console.log(err);
        this.errorMessage = err.error.message;
      }
    )
  }

  sectorSubmit() {
    if (this.sectorFormGroup.invalid) {
      console.log(this.sectorFormGroup.errors);
      return;
    }

    const {id, name} = this.sectorFormGroup.value;

    console.log(this.sectorFormGroup.value);
    console.log("sector Id :" + id);

    this.sectorService.addSector({id, name}).subscribe(
      (sector) => {
        console.log(sector)
      },
      (err) => {
        console.log(err.error.message)
        this.errorMessage = err.error.message;
      }
    )


  }
}
